(ns fpclj-finances.filter-transaction-test
  (:require [midje.sweet :refer :all]
            [fpclj-finances.acceptance-test-helpers :refer [default-port start-server stop-server content send-req]]
            [fpclj-finances.db :as db]))

(def some-transactions [{:type "revenue" :value 10 :tags ["job" "coding"]}
                        {:type "revenue" :value 5.5 :tags ["job" "consulting"]}
                        {:type "expense" :value 3.45 :tags ["icecream" "food"]}
                        {:type "expense" :value 7.10 :tags ["juice"]}
                        {:type "revenue" :value 15.70 :tags ["random" "tag"]}])

(defn- register-transactions []
  (doseq [transaction some-transactions] (db/register transaction)))

(against-background [(before :facts [(start-server default-port) (db/clear-transactions) (register-transactions)])
                     (after :facts (stop-server))]

                    (fact "Sum of revenues is 31.20 with three transactions" :acceptance
                          (let [revenues (content "/revenue")]
                            (count revenues) => 3
                            (reduce + (map :value revenues)) => 31.20))

                    (fact "Sum of expenses is 10.55 with two transactions" :acceptance
                          (let [expenses (content "/expense")]
                            (count expenses) => 2
                            (reduce + (map :value expenses)) => 10.55))

                    (fact "There are five transactions"
                          (let [transactions (content "/transaction?tags=")]
                            (count transactions) => 5))

                    (fact "There are two transactions tagged as job" :acceptance
                          (let [transactions (content "/transaction?tags=job")]
                            (count transactions) => 2))

                    (fact "There are three transactions with tags icecream juice and random" :acceptance
                          (let [transactions (content "/transaction?tags=icecream&tags=juice&tags=random")]
                            (count transactions) => 3)))