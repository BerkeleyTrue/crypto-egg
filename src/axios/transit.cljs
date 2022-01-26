(ns axios.transit
  (:require
    [taoensso.timbre :as log]
    [axios.core :as axios]
    [cognitect.transit :as t]
    [com.wsscode.pathom3.connect.operation.transit :as p.transit]
    [utils]))

(def ^:private transit-content-type "application/transit+json")

(def ^:private writer
  (t/writer :json {:transform t/write-meta
                   :handlers p.transit/write-handlers}))

(def ^:private reader
  (t/reader :json {:handlers p.transit/read-handlers}))

(def client
  (axios/create-client
    {:base-url "http://localhost:3000"
     :headers
     {"Content-Type" transit-content-type
      "Accept" transit-content-type}
     :transform-request
     [(fn transform-request [data headers]
        (log/info "request" data)
        (let [content-type (get (js->clj headers) "Content-Type")]
          (if (= content-type transit-content-type)
            (t/write writer data)
            data)))]

     :transform-response
     [(fn transform-response [data headers]
        (log/info "response" data (type data))
        (let [content-type (:content-type (utils/js->cljkk headers))]
          (log/info "ct: " (= content-type transit-content-type))
          (let [data (if (= content-type transit-content-type)
                       (t/read reader data)
                       data)]
            (log/info data (type data))
            data)))]}

    {:cljify false}))


(axios/add-response-interceptor
  client
  (fn [response]
    (log/info (.-data response))
    (:data (utils/js->cljkk response))))


(comment
  (-> (axios/post client "/api" [{[:coin/id "btc"] [:coin/price]}])
      (.then #(do (log/debug "res: " (type %))
                  (js/console.log %)))))
