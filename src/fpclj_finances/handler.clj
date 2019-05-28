(ns fpclj-finances.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [cheshire.core :refer [generate-string]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-body]]
            [fpclj-finances.db :as db]
            [fpclj-finances.transactions :as trn]))

(defn- balance [value] {:headers {"Content-Type" "application/json; charset=utf-8"}
                        :throw-exceptions false
                        :body (generate-string {:balance value})})

(defn- invalid-transaction [] {:headers {"Content-Type" "application/json; charset=utf-8"}
                               :throw-exceptions false
                               :status 422
                               :body (generate-string {:message "Invalida data"})})

(defn- transaction [data] {:content-type :json
                           :throw-exceptions false
                           :status 201
                           :body (generate-string data)})

(defn- transactions [data] {:content-type :json
                            :throw-exceptions false
                            :body (generate-string data)})

(defroutes app-routes
  (GET "/balance" [] (balance (db/balance)))
  (GET "/transaction" {filters :params}
    (if (empty? filters)
      (transactions (db/transactions))
      (transactions (db/transactions-with-tags filters))))
  (GET "/revenue" [] (transactions (db/revenue)))
  (GET "/expense" [] (transactions (db/expense)))
  (POST "/transaction" data
    (let [trn-data (:body data)]
      (if (trn/valid? trn-data)
        (transaction (db/register trn-data))
        (invalid-transaction))))
  (route/not-found "Not Found"))

(def app
  (->
   (wrap-defaults app-routes api-defaults)
   (wrap-json-body {:keywords? true :bigdecimals? true})))
