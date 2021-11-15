(ns cli.utils)

(comment
  (.toFixed 2.004332334 6)
  (.toFixed 2.004332334 0)
  (condp > 2.343
    0.001 "foo"
    0.01 "bar"
    0.1 "baz"
    1 (.toFixed 2.323 4)
    100 (.toFixed 2.323 2)
    "default"))

(defn display-price [price]
  (condp > price
    0.0001 "> 0.001"
    0.01 (.toFixed price 6)
    1 (.toFixed price 4)
    100 (.toFixed price 2)
    (.toFixed price 0)))

(comment
  (display-price 0.000234928374)
  (display-price 0.0234987342)
  (display-price 0.23423847)
  (display-price 2.343234245178)
  (display-price 23.43234245178)
  (display-price 234.3234245178))
