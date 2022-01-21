(ns server.services.coingecko.core
  (:require
    [clojure.string :as string]
    [cljs.pprint :refer [pprint]]
    [integrant.core :as ig]
    [taoensso.timbre :as tb :refer-macros [info error]]
    [datascript.core :as d]
    [rx.core :as rx]
    [rx.operators :as op]
    ["axios" :as axios]
    [utils]))

(def api "https://api.coingecko.com/api/v3/")

(def client (.create axios (clj->js {:baseURL api})))

(defn ping-ok []
  (->
    (rx/defer #(.get client "/ping"))
    ((rx/pipe
      (op/map utils/js->cljkk)
      (op/map :status)
      (op/map #(= 200 %))
      (op/catch-error
        (fn [err]
          (error err)
          (rx/of false)))))))


(defn get-coins [coins]
  (->
    (rx/defer
      #(.get client
        "/coins/markets"
        (utils/cljkk->js
          {:params
           {:vs_currency "usd"
            :ids
            (string/join ", " coins)}})))
    ((rx/pipe
      (op/map utils/js->cljkk)
      ; (op/tap #(info %))
      (op/map :data)
      (op/concat-map identity)
      (op/map (fn [{:keys [id ath symbol current_price]}]
                {:id id
                 :ath ath
                 :sym symbol
                 :price current_price}))
      ; (op/tap #(do (tb/info "----") (tb/info %)))
      (op/catch-error
        (fn [err]
          (js/console.error err)
          rx/EMPTY))))))

(defmethod ig/init-key :service/coingecko
  [_ {:keys [coins conn]}]
  (info "coingecko service started")
  (->
    (ping-ok)
    ((rx/pipe
       (op/switch-map
         (fn [ok]
           (if ok
             (do (tb/info "ping ok")
               (rx/interval 5000))
             (do
               (tb/warn "ping not ok")
               rx/EMPTY))))
       (op/switch-map #(get-coins coins))
       ; {:id :ath :sym :price}
       (op/tap
         (fn [{:keys [id ath sym price]}]
           (d/transact! conn
             [{:db/id id
               :coin/id id
               :coin/ath ath
               :coin/sym sym
               :coin/price price}])))))
    (.subscribe (fn []))))


(defmethod ig/halt-key! :service/coingecko
  [_ subscription]
  (when (.-unsubscribe subscription)
    (info "unsubscribing")
    (.unsubscribe subscription)))
