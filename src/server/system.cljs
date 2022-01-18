(ns server.system
  (:require
    [integrant.core :as ig]
    [macchiato.server :as http]
    [taoensso.timbre :refer-macros [info error]]))


(defmethod ig/init-key :system/http
  [_ {:keys [port handler host]}]
  (info "starting server")
  (http/start {:host host
               :port port
               :handler handler
               :on-success
               #(info "server started on" host ":" port)}))

(defmethod ig/halt-key! :system/http
  [_ server]
  (info "closing server")
  (when server
    (.close
      server
      (fn [err]
        (info "server stopped")
        (when err (error err))))))
