(ns server.infra.graceful-shutdown
  (:require
    [taoensso.timbre :refer [error info]]))

(def ^:private handlers* (atom []))

(goog-define TEST? false)

(defn add-graceful-exit-handler [handler]
  (swap! handlers* conj handler))

(defn- graceful-dance []
  (let [handlers @handlers*]
    (run! #(%) handlers)))

(defn init []
  (js/process.on
    "uncaughtException"
    (fn [err]
      (if-let [cause (ex-cause err)]
        (error cause)
        (error err))
      (when-let [data (ex-data err)]
        (info data))
      (graceful-dance)
      (js/process.exit 1)))

  (js/process.on
    "SIGINT"
    (fn []
      (info "SIGINT: halting")
      (graceful-dance)
      (info "exiting")
      (js/process.exit))))

; don't run in test env
(when-not TEST? (init))
