(ns app.main
  (:require [eosjs]
            [eosjs-api :as api]
            [scatterjs-core :as sc]
            [scatterjs-plugin-eosjs2 :as sceos]))

;; Connect with ScatterJS library
;;
(def scatter_js (.-scatter (.-ScatterJS js/window)))
(def scatter_eos (.-ScatterEOS js/window))

;; Network settings
;;
(def network_info #js {:blockchain "eos" :protocol "https" :host "nodes.get-scatter.com" :port 443 :chainId "aca376f206b8fc25a6ed44dbdc66547c36c6c33e3a119ffbeaef943642f0e906"})
(def network (.fromJson (.-Network sc) network_info))

;; Helpers
;;
(defn userName [] (.-name (.-identity scatter_js)))
(defn publicKey [] (.-publicKey (.-identity scatter_js)))

;; Push EOS actions
;;
(def tokenDetails #js {:contract "eosio.token" :symbol "EOS" :memo "heelloo", :decimals 4})
(defn requestTransfer [to amount] (-> (.requestTransfer scatter_js network to amount tokenDetails)(.then #(.log js/console "result" %))))
;(defn requestTransfer [to amount] (.requestTransfer scatter_js network to amount tokenDetails))

(def transactionData #js {:id "random-id" :origin "EOS Mail" :blockchain "eos" :actions #js [{:contract "eosio.token" :action "transfer" :params #js ["eosnavigator" "eosio" "0.0001 EOS" ""]}]})
(defn createTransaction [] (.log js/console (.createTransaction scatter_js transactionData)))
                               ;(.then #(.log js/console "trans result" %))))

;; Probably not needed at all
;; (defn loadPlugin [] (.loadPlugin scatter_js scatter_eos))

;;  API to get info
;;
(defn getPublicKey [blockchain] (-> (.getPublicKey scatter_js)
                                    (.then #(.log js/console "pubkey" %))))


(def apiOptions #js {:httpEndpoint "https://nodes.get-scatter.com:443" :verbose false :fetchConfiguration {}})
(def eosapi (eosjs apiOptions))

(defn getAccount [account_name] (-> (.getAccount eosapi account_name) (.then #(.log js/console "account" %))))
(defn getBlock [block_num] (-> (.getBlock eosapi block_num) (.then #(.log js/console "block 1" %))))
(defn getCurrencyBalance [symbol contract account_name] (-> (.getCurrencyBalance eosapi contract account_name symbol) (.then #(.log js/console (str symbol "balance") %))))
(defn getActions [account_name] (-> (.getActions eosapi account_name 0 2000) (.then #(.log js/console (str account_name " actions") %))))
(defn getInfo [] (-> (.getInfo eosapi {})
                     (.then #(.log js/console "info" %))))

;; What to do when connected to scatter
;;

(defn connected [identity]
  (.log js/console "Connected to Scatter...")
  (.log js/console "account" (userName))
  (.log js/console "pubKey" (publicKey))
  (aset js/window "ScatterJS" "null")
  (.log js/console "getInfo" (getInfo))
  (.log js/console "getBlock" (getBlock 1))
  (.log js/console "getAccount" (getAccount "eosnavigator"))
  (.log js/console "getBalance" (getCurrencyBalance "EOS" "eosio.token" "eosnavigator"))
  (.log js/console "getBalance" (getCurrencyBalance "DICE" "betdicetoken" "eosnavigator"))
  (.log js/console "getActions" (getActions "eosnavigator"))
  (requestTransfer "ikhalilsofia" 0.0001))
  ;(createTransaction))

;;
;; Connect to Scatter
;;
(def requiredFields #js {:accounts #js [network]})
(defn connect_scatter [name] (-> (.connect scatter_js name)(.then #(.suggestNetwork scatter_js network))(.then #(.getIdentity scatter_js requiredFields))(.then #(connected %))))

;; Main
;;
;;
(defn main! []
  (println "Started!")
  ;(.log js/console "network" network)
  ;(.log js/console "eosjs" (eosjs))
  (.log js/console "scatter_js" scatter_js)
  ;(.log js/console "scatter_eos" scatter_eos)
  ;(.log js/console "load_plugin" (loadPlugin))
  ;(.connect scatter_js "EOS Mail"))
  (connect_scatter "EOS Mail"))

(defn reload! []
  (main!)
  (println "Reloaded!"))
