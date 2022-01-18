(ns server.routes
  (:require
    [macchiato.util.response :as r]
    [integrant.core :as ig]))

(defn home [_ res _]
  (-> {:message "ok"}
      (r/ok)
      (r/json)
      (res)))

(defn not-found [_ res _]
  (-> {:message "404"}
      (r/not-found)
      (r/json)
      (res)))

(def routes
  ["/" {:get home}])

(defn router [req res raise]
  (home req res raise))

(defmethod ig/init-key :router/handler [] router)
