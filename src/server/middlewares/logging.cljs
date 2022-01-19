(ns server.middlewares.logging
  (:require [clojure.string]
            ["chalk" :as chalk]))

(comment (print (.green chalk "green")))

(defn- get-req-method [request]
  (-> (:request-method request)
      (name)
      (clojure.string/upper-case)))


(defn- get-status-color [status]
  (condp > status
    300 #(.green chalk %)
    300 #(.blue chalk %)
    400 #(.red chalk %)
    500 #(.yellow chalk %)
    600 #(.red chalk %)
    :else #(.red chalk %)))

(defn- print-res-status [{:keys [status]}]
  ((get-status-color status) status))

(comment (print-res-status {:status 200}))

(defn- print-req-method [request res-status]
  (let [color (get-status-color res-status)]
    (-> request
        (get-req-method)
        (color)
        (#(.bold chalk %)))))

(comment (print ((.. chalk -cyan -bold) "foo")))

(defn- print-req-path [{:keys [uri]}]
  ((.. chalk -bold -cyan) uri))

(defn- create-response-logger [request respond]
  (let [startTime (js/Date.now)]
    (fn [res-map]
      (let [endTime (js/Date.now)
            total-time (- endTime startTime)]
        (print
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
