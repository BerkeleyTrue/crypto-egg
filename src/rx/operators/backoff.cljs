(ns rx.operators.backoff
  (:require
    ["backoff-rxjs" :as backoffjs]
    [utils :refer [cljkk->js]]))

(defn backoff [conf]
  (.retryBackoff backoffjs (cljkk->js conf)))

(comment (backoff {}))
