(ns fpclj-finances.balance-acceptance-test
  (:require [fpclj-finances.acceptance-test-helpers :refer [default-port start-server stop-server content send-req]]
            [midje.sweet :refer :all]
            [fpclj-finances.db :as db]
            [cheshire.core :refer [generate-string]]))

(against-background [(before :facts [(start-server default-port) (db/clear-transactions)]) (after :facts (stop-server))]

                    (fact "Initial balance is zero" :acceptance
                          (content "/balance") => {:balance 0})

                    (fact "Balance is 10 when there is one revenue transaction with value 10" :acceptance
                          (send-req "/transaction" {:body "{\"value\" : 10, \"type\": \"revenue\"}" :content-type :json})
                          (content "/balance") => {:balance 10})

                    (fact "Balance is 50 with one revenue of 100 and a expense of 50" :acceptance
                          (send-req "/transaction" {:body "{\"value\" : 100, \"type\": \"revenue\"}" :content-type :json})
                          (send-req "/transaction" {:body "{\"value\" : 50, \"type\": \"expense\"}" :content-type :json})
                          (content "/balance") => {:balance 50}))
