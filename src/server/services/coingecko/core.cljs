(ns server.services.coingecko.core
  (:require
    [integrant.core :as ig]
    [taoensso.timbre :refer-macros [info error]]
    [rx.core :as rx]
    [rx.operators :as op]
    ["axios" :as axios]))


(def api "https://api.coingecko.com/api/v3/")

(def client (.create axios (clj->js {:baseURL api})))


(defmethod ig/init-key :service/coingecko
  []
  (info "coingecko service started")
  (->
    (rx/defer #(.get client "/ping"))
    ((rx/pipe
      (op/map #(js->clj (or % "") :keywordize-keys true))
      (op/map :status)
      (op/map #(info (str "coingecko status: " %1)))
      (op/catch-error (fn [err] (error err)))))
    (.subscribe (fn []))))


(defmethod ig/halt-key! :service/coingecko
  [_ subscription]
  (when (.-unsubscribe subscription)
    (.unsubscribe subscription)))
