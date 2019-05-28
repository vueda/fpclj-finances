(ns fpclj-finances.handler-test
  (:require [midje.sweet :refer :all]
            [ring.mock.request :as mock]
            [fpclj-finances.handler :refer :all]
            [fpclj-finances.db :as db]
            [cheshire.core :refer [generate-string parse-string]]))

(def zero-balance "{\"balance\":0}")
(def success-code 200)
(def not-found-code 404)
(def revenue-transaction {:body {:value 10 :type "revenue"}})

(facts "Invalid routes"
       (let [response (app (mock/request :get "/something"))]
         (fact "Error code is 404"  (:status response) => not-found-code)
         (fact "Response body is Not Found" (:body response) => "Not Found")))

(facts "Initial balance is zero"
       (against-background [(generate-string {:balance 0}) => zero-balance]
                           (let [response (app (mock/request :get "/balance"))]
                             (fact "Response status is 200" (:status response) => success-code)
                             (fact "Response body is zero" (:body response) => zero-balance))))

(facts "Register a revenue transaction with value of 10"
       (against-background (db/register {:value 10, :type "revenue"}) => {:id 9 :value 10, :type "revenue"})
       (let [response (app (->
                            (mock/request :post "/transaction" revenue-transaction)
                            (mock/json-body {:value 10 :type "revenue"})))]
         (fact "Response status is 201"
               (:status response) => 201)

         (fact "Response body is revenue transaction with id"
               (:body response) => "{\"id\":9,\"value\":10,\"type\":\"revenue\"}")

         (fact "No transactions are really added to the atom because of mock in against-background"
               (count (db/transactions)) => 0)))

(facts "List expense transactions"
       (against-background (db/expense) => [{:type "expense" :value 10} {:type "expense" :value 1} {:type "expense" :value 3}])

       (let [response (app (mock/request :get "/expense"))]

         (fact "There are three expenses"
               (count (parse-string (:body response))) => 3)))

(facts "List revenue transactions"
       (against-background (db/revenue) => [{:type "revenue" :value 10} {:type "revenue" :value 1}])

       (let [response (app (mock/request :get "/revenue"))]

         (fact "There are two revenues"
               (count (parse-string (:body response))) => 2)))

(facts "List all transactions"
       (against-background (db/transactions) => [{:type "revenue" :value 10} {:type "expense" :value 1}])

       (let [response (app (mock/request :get "/transaction"))]

         (fact "There are two transactions"
               (count (parse-string (:body response))) => 2)))

(facts "Transactions filtered by tags"
       (against-background (db/transactions-with-tags {:tags ["rev" "exp"]}) => [{:type "revenue" :value 10} {:type "expense" :value 10}])

       (let [response (app (mock/request :get "/transaction?tags=rev&tags=exp"))]

         (fact "There are two transactions with tags"
               (count (parse-string (:body response))) => 2)))