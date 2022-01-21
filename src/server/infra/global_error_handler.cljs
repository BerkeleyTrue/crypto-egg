(ns server.infra.global-error-handler
  (:require
    [cljs.pprint :refer [pprint]]
    [taoensso.timbre :refer [error]]))

(js/process.on
  "uncaughtException"
  (fn [err]
    (if-let [cause (ex-cause err)]
      (error cause)
      (error err))
    (when-let [data (ex-data err)]
      (pprint data))
    (js/process.exit 1)))
