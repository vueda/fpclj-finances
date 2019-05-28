(ns fpclj-finances.transactions)

(defn- valid-value? [trn] (and
                           (contains? trn :value)
                           (number? (:value trn))
                           (pos? (:value trn))))

(defn- valid-type? [trn] (and
                          (contains? trn :type)
                          (or (= "revenue" (:type trn))
                              (= "expense" (:type trn)))))

(defn valid? [trn] (and (valid-type? trn)
                        (valid-value? trn)))