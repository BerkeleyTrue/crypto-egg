(ns server.config
  (:require [integrant.core :as ig]))

(def env
  {:system/http
   {:port 3000
    :host "127.0.0.1"
    :handler (ig/ref :router/handler)}
   :router/handler {}})
