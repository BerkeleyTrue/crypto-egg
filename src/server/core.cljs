(ns server.core
  (:require
    ; [server.middleware :refer [wrap-defaults]]
    [server.routes]
    [server.system]
    [server.config :refer [env]]
    ; [macchiato.middleware.session.memory :as mem]
    [taoensso.timbre :refer-macros [info]]
    [integrant.core :as ig]))


(defonce system-ref (atom nil))
(defmethod ig/init-key :router/foo [] (fn []))

(defn ^:dev/after-load main []
  (info "hot load")
  (reset! system-ref (ig/init env)))

(defn ^:dev/before-load stop []
  (info "cooldown")
  (when-some [system @system-ref]
    (ig/halt! system)
    (reset! system-ref nil)))
