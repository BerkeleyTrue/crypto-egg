(ns server.infra.timbre
  (:require
    [cljs.pprint :refer [pprint]]
    [taoensso.timbre :as timbre]
    [chalk.core :as chalk]))

(def ^:private level-to-color
  {:debug chalk/cyan
   :info chalk/blue
   :warn chalk/yellow
   :error chalk/red
   :fatal chalk/bg-red})

(defn- timbre-output-wrap
  ([data] (timbre-output-wrap nil data))
  ([opts data]
   (let [level (:level data)
         color-fn (get level-to-color level chalk/white)
         res (timbre/default-output-fn opts data)]
     (color-fn res))))

(defn- pp-data-middleware
  [data]
  (update
    data
    :vargs
    (partial
      mapv
      #(if (string? %)
        %
        (with-out-str (pprint %))))))

(timbre/merge-config!
  {:timestamp-opts {:pattern "HH:MM:SS"}
   :output-fn timbre-output-wrap
   :middleware [pp-data-middleware]})

(comment
  (timbre/debug "debug")
  (timbre/info "info")
  (timbre/warn "warn")
  (timbre/error "error")
  (timbre/fatal "fatal"))
