(ns fpclj-finances.db
  (:require [clojure.string :refer [blank?]]))

(defn- expense? [transaction]
  (= (:type transaction) "expense"))

(defn- calculate-balance [acc transaction]
  (let [value (:value transaction)]
    (if (expense? transaction)
      (- acc value)
      (+ acc value))))

(defn- filter-transaction [value key]
  (filter #(= value (get % key)) @entries))

(def entries (atom []))

(defn clear-transactions [] (reset! entries []))

(defn transactions [] @entries)

(defn transactions-with-tags [filters]
  (let [tags (->>
              (:tags filters)
              (conj [])
              (flatten)
              (remove blank?)
              (set))]
    (filter #(or (empty? tags) (some tags (:tags %))) @entries)))

(defn revenue [] (filter-transaction "revenue" :type))

(defn expense [] (filter-transaction "expense" :type))

(defn balance []
  (reduce calculate-balance 0 @entries))

(defn register [data]
  (let [updated-entries (swap! entries conj data)]
    (merge data {:id (count updated-entries)})))