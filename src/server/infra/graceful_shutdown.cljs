(ns server.infra.graceful-shutdown
  (:require
    [taoensso.timbre :refer [error info]]))

(def ^:private handlers* (atom []))

(goog-define TEST? false)

(defn add-graceful-exit-handler [handler]
  (swap! handlers* conj handler))

(def ^:private kill-signals
  ["SIGTERM"
   "SIGINT"])

(defn- graceful-dance []
  (let [handlers @handlers*]
    (run! #(%) handlers)))

(defn- signal-kill [signal]
  (js/process.on
    signal
    (fn []
      (info (str signal ": halting"))
      (graceful-dance)
      (info "exiting")
      (js/process.exit))))

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

  (run! signal-kill kill-signals))

; don't run in test env
(when-not TEST? (init))
