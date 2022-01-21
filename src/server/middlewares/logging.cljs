(ns server.middlewares.logging
  (:require
    [clojure.string]
    [taoensso.timbre :refer-macros [info]]
    [chalk.core :refer [green blue red yellow bold cyan]]))

(comment (info (green "green")))

(defn- get-req-method [request]
  (-> (:request-method request)
      (name)
      (clojure.string/upper-case)))


(defn- get-status-color [status]
  (condp > status
    300 green
    300 blue
    400 red
    500 yellow
    600 red
    :else red))

(defn- print-res-status [{:keys [status]}]
  ((get-status-color status) status))

(comment (print-res-status {:status 200}))

(defn- print-req-method [request res-status]
  (let [color (get-status-color res-status)]
    (-> request
        (get-req-method)
        (color)
        (bold))))

(comment (print ((comp cyan bold) "foo")))

(defn- print-req-path [{:keys [uri]}]
  ((comp bold cyan) uri))

(defn- create-response-logger [request respond]
  (let [startTime (js/Date.now)]
    (fn [res-map]
      (let [endTime (js/Date.now)
            total-time (- endTime startTime)]
        (info
          (str
            (print-res-status res-map)
            " "
            (print-req-method request (:status res-map))
            " "
            (print-req-path request)
            " "
            total-time "ms"))
        (respond res-map)))))

(defn wrap-with-logger [handler]
  (fn [request respond raise]
    (handler request (create-response-logger request respond) raise)))
