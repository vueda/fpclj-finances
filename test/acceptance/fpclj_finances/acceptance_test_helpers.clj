(ns fpclj-finances.acceptance-test-helpers
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [fpclj-finances.handler :refer [app]]
            [clj-http.client :as http]
            [cheshire.core :refer [parse-string]]))

(def default-port 3001)

(def server (atom nil))

(defn- server-url [route] (str "http://localhost:" default-port route))

(def get-request (comp http/get server-url))

(defn content [route] (parse-string (:body (get-request route)) true))

(defn send-req [route data] (http/post (server-url route) data))

(defn start-server [port]
  (swap! server (fn [_] (run-jetty app {:port port :join? false}))))

(defn stop-server []
  (.stop @server))
