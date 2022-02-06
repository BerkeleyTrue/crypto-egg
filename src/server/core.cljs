(ns server.core
  (:require
    [taoensso.timbre :as log :refer-macros [info]]
    [integrant.core :as ig]
    [server.infra]
    [server.infra.graceful-shutdown :refer [add-graceful-exit-handler]]
    [server.app]
    [server.config :refer [env]]))

(defonce system-ref (atom nil))

(defn halt! []
  (when-some [system @system-ref]
    (ig/halt! system)
    (reset! system-ref nil)))

(add-graceful-exit-handler halt!)

(defn ^:dev/after-load main []
  (info "hot load")
  (reset! system-ref (ig/init env)))

(defn ^:dev/before-load stop []
  (info "cooldown")
  (halt!))
