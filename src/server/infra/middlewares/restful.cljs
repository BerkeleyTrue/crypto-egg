(ns server.infra.middlewares.restful
  (:require
    [cognitect.transit :as t]
    [com.wsscode.pathom3.connect.operation.transit :as p.transit]
    [macchiato.middleware.restful-format :as rf]))

(comment
  (let [writer (t/writer :json {:handlers p.transit/write-handlers
                                :transform t/write-meta})
        reader (t/reader :json {:handlers p.transit/read-handlers})

        wrote (t/write writer [{[:coin/sym "btc"] [:coin/price]}])
        readed (t/read reader wrote)]
    (print wrote)
    (print readed)))

(defn wrap-restful-format [handler]
  (rf/wrap-restful-format
    handler
    {:keywordize? true
     :transit-opts
     {:reader {:handlers p.transit/read-handlers}
      :writer {:handlers p.transit/write-handlers
               :transform t/write-meta}}}))
