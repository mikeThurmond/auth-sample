(ns auth.core
  (:require [buddy.sign.jwt :as jwt]
            [clj-time.core :as time]))

(defonce users (atom [{:email    "bob@bobsburgers.com"
                       :password "SecurePassword"}]))

(def secret "my super safe secret")

(def url "localhost:3449/verify?token=" )

(def message "You are receiving this because you (or someone else)
requested a password reset on your Conduit user account.
Please click the following link to complete the process:\n")

(defn get-user [user]
  (seq (filter #(= % user) @users)))

(defn query-email [email]
  (filter #(= (:email %) email) @users))

(defn send-email [{:keys [email] :as user-data}]
  (println email)
  (when (seq (query-email email))
    (let [user-data (assoc user-data :exp (time/plus (time/now) (time/seconds 600)))
          token (jwt/sign user-data secret)]

      ;;todo
      (println (str message url token))

      (str message url token)

      )))

(defn verify-password [{:keys [token password]}]
  (try
    (let [email (:email (jwt/unsign token secret))
          user (first (query-email email))
          index (.indexOf @users user)
          _ (swap! users assoc-in [index :password] password)]
      true)
    (catch Exception e
      false)))
