(ns server.services.coingecko.core
  (:require
    [clojure.string :as string]
    [cljs.pprint :as pprint]
    [integrant.core :as ig]
    [taoensso.timbre :refer-macros [info error]]
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


(defn get-coins []
  (->
    (rx/defer
      #(.get client
        "/coins/markets"
        (utils/cljkk->js
          {:params
           {:vs_currency "usd"
            :ids
            (string/join ", "
              ["bitcoin"
               "ethereum"
               "tezos"
               "pickle-finance"
               "olympus"
               "ethereum-name-service"])}})))
    ((rx/pipe
      (op/map utils/js->cljkk)
      (op/map :data)
      (op/concat-map identity)
      (op/map (fn [{:keys [id ath symbol current_price]}]
                {:id id
                 :ath ath
                 :sym symbol
                 :price current_price}))
      (op/map #(do
                 (println "----")
                 (pprint/pprint %)
                 (println "----")))
      (op/catch-error
        (fn [err]
          (js/console.error err)
          rx/EMPTY))))))

(defmethod ig/init-key :service/coingecko
  []
  (info "coingecko service started")
  (->
    (ping-ok)
    ((rx/pipe
       (op/switch-map
         (fn [ok]
           (if ok
             (get-coins)
             rx/EMPTY)))))
    (.subscribe (fn []))))


(defmethod ig/halt-key! :service/coingecko
  [_ subscription]
  (when (.-unsubscribe subscription)
    (.unsubscribe subscription)))
