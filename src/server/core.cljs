(ns server.core
  (:require
    [cljs.pprint :refer [pprint]]
    [taoensso.timbre :as timbre :refer-macros [info error]]
    [integrant.core :as ig]
    [datascript.core :as d]
    [server.routes]
    [server.system]
    [server.config :refer [env]]
    [server.services.coingecko.core]))

(js/process.on
  "uncaughtException"
  (fn [err]
    (if-let [cause (ex-cause err)]
      (error cause)
      (error err))
    (when-let [data (ex-data err)]
      (pprint data))
    (js/process.exit 1)))

(timbre/merge-config!
  {:timestamp-opts {:pattern "HH:MM:SS"}})


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
