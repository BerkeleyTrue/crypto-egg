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
    {:headers
     {"Content-Type" transit-content-type
      "Accept" transit-content-type}
     :transform-request
     [(fn transform-request [data headers]
        (let [content-type (get (js->clj headers) "Content-Type")]
          (if (= content-type transit-content-type)
            (t/write writer data)
            data)))]
     
     :transform-response
     [(fn transform-response [data headers]
        (let [content-type (:content-type (utils/js->cljkk headers))]
          (if (= content-type transit-content-type)
            (t/read reader data)
            data)))]}
    
    {:cljify false}))


(axios/add-response-interceptor
  client
  (fn [response]
    (.-data response)))

(comment
  (-> (axios/post client "http://localhost:3000/api" [{[:coin/sym "xtz"] [:coin/price]}])
      (.then #(do (log/debug "res: " %)))))
