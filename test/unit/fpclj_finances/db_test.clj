(ns fpclj-finances.db-test
  (:require [midje.sweet :refer :all]
            [fpclj-finances.db :refer :all]))

(defn new-revenue-transaction [val] {:value (* 7 (+ 1 val)) :type "revenue" :tags ["trn" "rev"]})
(defn new-expense-transaction [val] {:value (* 3 (+ 1 val)) :type "expense" :tags ["trn" "exp"]})

(facts "Store transactions in atom"
       (against-background [(before :facts (clear-transactions))
                            (after :facts (clear-transactions))]

                           (fact "Starts with empty transactions"
                                 (count (transactions)) => 0)

                           (fact "Add one transaction and change balance"
                                 (register (new-revenue-transaction 1)) => (assoc (new-revenue-transaction 1) :id 1)
                                 (count (transactions)) => 1
                                 (balance) => 14)

                           (fact "Add only revenue transactions and balance is the sum of them"
                                 (dotimes [idx 3] (register (new-revenue-transaction idx)))
                                 (count (transactions)) => 3
                                 (balance) => 42)

                           (fact "Add only expense transactions and balance is negative"
                                 (dotimes [idx 3] (register (new-expense-transaction idx)))
                                 (count (transactions)) => 3
                                 (balance) => -18)

                           (fact "Balance is sum of revenue minus expenses"
                                 (register (new-revenue-transaction 1))
                                 (register (new-expense-transaction 1))
                                 (count (transactions)) => 2
                                 (balance) => 8)

                           (fact "There are two revenues"
                                 (register (new-revenue-transaction 1))
                                 (register (new-revenue-transaction 1))
                                 (register (new-expense-transaction 1))
                                 (count (revenue)) => 2)

                           (fact "There are three expenses"
                                 (register (new-expense-transaction 1))
                                 (register (new-revenue-transaction 1))
                                 (register (new-expense-transaction 1))
                                 (register (new-revenue-transaction 1))
                                 (register (new-expense-transaction 1))
                                 (count (expense)) => 3)

                           (fact "There are four transactions"
                                 (register (new-expense-transaction 1))
                                 (register (new-revenue-transaction 1))
                                 (register (new-expense-transaction 1))
                                 (register (new-revenue-transaction 1))
                                 (count (transactions)) => 4)

                           (fact "There are two transactions with tag rev"
                                 (register (new-revenue-transaction 1))
                                 (register (new-revenue-transaction 1))
                                 (register (new-expense-transaction 1))
                                 (count (transactions-with-tags {:tags ["rev"]})) => 2)

                           (fact "There are three transactions with tag trn"
                                 (register (new-revenue-transaction 1))
                                 (register (new-revenue-transaction 1))
                                 (register (new-expense-transaction 1))
                                 (count (transactions-with-tags {:tags ["trn"]})) => 3)

                           (fact "There are three transactions with tags rev and exp"
                                 (register (new-revenue-transaction 1))
                                 (register (new-revenue-transaction 1))
                                 (register (new-expense-transaction 1))
                                 (count (transactions-with-tags {:tags ["rev" "exp"]})) => 3)

                           (fact "There are three transactions with no tag filter"
                                 (register (new-revenue-transaction 1))
                                 (register (new-revenue-transaction 1))
                                 (register (new-expense-transaction 1))
                                 (count (transactions-with-tags {:tags []})) => 3)))

