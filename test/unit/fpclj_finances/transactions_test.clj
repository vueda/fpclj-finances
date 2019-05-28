(ns fpclj-finances.transactions-test
  (:require [midje.sweet :refer :all])
  (:require [fpclj-finances.transactions :refer :all]))

(def transaction-without-value {:type "revenue"})
(def transaction-with-negative-value {:value -10})
(def transaction-with-zero-value {:value 0})
(def transaction-with-value-not-number {:value "other"})
(def transaction-without-type {:value 10})
(def transaction-with-invalid-type {:type "unknown"})
(def transaction {:value 10 :type "revenue"})

(fact "A transaction without value is invalid"
      (valid? transaction-without-value) => falsey)

(fact "A transaction with negative value is invalid"
      (valid? transaction-with-negative-value) => falsey)

(fact "A transaction with zero value is invalid"
      (valid? transaction-with-zero-value) => falsey)

(fact "A transaction with non numeric value is invalid"
      (valid? transaction-with-value-not-number) => falsey)

(fact "A transaction without type is invalid"
      (valid? transaction-without-type) => falsey)

(fact "A transaction with unknown type is invalid"
      (valid? transaction-with-invalid-type) => falsey)

(fact "A transaction is valid with positive value and known type"
      (valid? transaction) => truthy)