(ns server.core
  (:require
    [taoensso.timbre :as timbre :refer-macros [info]]
    [integrant.core :as ig]
    [datascript.core :as d]
    [server.infra.core]
    [server.routes]
    [server.system]
    [server.config :refer [env]]
    [server.services.coingecko.core]))


(defonce system-ref (atom nil))

(defmethod ig/init-key :db/conn [_ {:keys [schema]}]
  (d/create-conn schema))

(defn ^:dev/after-load main []
  (info "hot load")
  (reset! system-ref (ig/init env)))

(defn ^:dev/before-load stop []
  (info "cooldown")
  (when-some [system @system-ref]
    (ig/halt! system)
    (reset! system-ref nil)))
