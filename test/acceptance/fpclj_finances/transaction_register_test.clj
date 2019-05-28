(ns fpclj-finances.transaction-register-test
  (:require [fpclj-finances.acceptance-test-helpers :refer [default-port start-server stop-server content send-req]]
            [midje.sweet :refer :all]
            [fpclj-finances.db :as db]
            [cheshire.core :refer [generate-string]]))

(def transaction-without-value {:body "{\"type\": \"revenue\"}" :content-type :json :throw-exceptions false})
(def transaction-without-type {:body "{\"value\": 10}" :content-type :json :throw-exceptions false})
(def transaction-with-negative-value {:body "{\"value\": -10,\"type\": \"revenue\"}" :content-type :json :throw-exceptions false})
(def transaction-with-invalid-value {:body "{\"value\": 10, \"type\": \"something\"}" :content-type :json :throw-exceptions false})

(against-background [(before :facts [(start-server default-port) (db/clear-transactions)])
                     (after :facts (stop-server))]

                    (fact "Reject a transaction without value" :acceptance
                          (let [response (send-req "/transaction" transaction-without-value)]
                            (:status response) => 422))

                    (fact "Reject a transaction with negative value" :acceptance
                          (let [response (send-req "/transaction" transaction-with-negative-value)]
                            (:status response) => 422))

                    (fact "Reject a transaction without type" :acceptance
                          (let [response (send-req "/transaction" transaction-without-type)]
                            (:status response) => 422))

                    (fact "Reject a transaction with invalid type" :acceptance
                          (let [response (send-req "/transaction" transaction-with-invalid-value)]
                            (:status response) => 422)))