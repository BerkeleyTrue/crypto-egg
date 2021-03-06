(ns server.app.services.coingecko
  (:require
    [clojure.string :as string]
    [integrant.core :as ig]
    [taoensso.timbre :as log :refer-macros [info error]]
    [datascript.core :as d]
    [rx.core :as rx]
    [rx.operators :as op]
    [rx.operators.backoff :refer [backoff]]
    [axios.core :as axios]
    [utils]))

(def api "https://api.coingecko.com/api/v3/")

(def client (axios/create-client {:base-url api}))

(defn ping-ok []
  (->
    (rx/defer #(axios/get client "/ping"))
    ((rx/pipe
       (backoff {:reset-on-success true
                 :initial-interval 2000
                 :max-interval (* 10 1000)
                 :should-retry
                 (fn []
                   (info "ping attempt failed")
                   true)})
       (op/map :status)
       (op/map #(= 200 %))
       (op/catch-error
         (fn [err]
           (error err)
           (rx/of false)))))))


(defn get-coins [coins]
  (->
    (rx/defer
      #(axios/get client
        "/coins/markets"
        {:params
         {:vs_currency "usd"
          :ids (string/join ", " coins)}}))

    ((rx/pipe
       (op/map :data)
       (op/concat-map identity)
       (op/map
         (fn [{:keys [id ath symbol current_price]}]
           {:id id
            :ath ath
            :sym symbol
            :price current_price}))
       ; (op/tap #(do (log/info "----") (log/info %)))
       (op/catch-error
         (fn [err]
           (error err)
           rx/EMPTY))))))

(defmethod ig/init-key :app.service/coingecko
  [_ {:keys [coins conn]}]
  (info "coingecko service started")
  (->
    (ping-ok)
    ((rx/pipe
       (op/switch-map
         (fn [ok]
           (if ok
             (do (log/info "ping ok")
               (rx/interval 5000))
             (do
               (log/warn "ping not ok")
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


(defmethod ig/halt-key! :app.service/coingecko
  [_ subscription]
  (when (.-unsubscribe subscription)
    (info "unsubscribing")
    (.unsubscribe subscription)))
