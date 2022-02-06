(ns axios.interceptor
  (:require
    [axios.utils :refer [config->js is-client?]]
    [goog.object :as obj]
    [utils]))

; request interceptor can be async or sync
(defn wrap-request-interceptor [h]
  (fn wrapped-request-handler [config]
    (-> config
      (utils/js->cljkk)
      (h)
      (#(if (fn? (.-then %))
         (.then % config->js)
         (config->js %))))))

(defn create-add-interceptor
  "Create add-interceptor function.
  (add-interceptor client then-handler? catch-handler? opts?)
  "
  ([method] (create-add-interceptor method {}))
  ([method {:keys [wrap-then wrap-catch]}]
   (fn add-interceptor [^js/Axios client & args]
     {:pre [(is-client? client)]}
     (let [then-handler (when-let [then-handler (first args)]
                          (if wrap-then
                            (wrap-then then-handler)
                            then-handler))
           
           catch-handler (when-let [catch-handler (second args)]
                           (if wrap-catch
                             (wrap-catch catch-handler)
                             catch-handler))
           opts (nth args 2 nil)]
       (js-invoke
         (obj/get (.. client -interceptors) method)
         "use"
         then-handler
         catch-handler
         opts)))))
