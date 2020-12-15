import React from "react";
import "./SignUp.css";
import SignUpForm from "./SignUpForm"

export default function SignUp() {
  return (
      <div className="Container">
        <div className="SignUpBox">
          <SignUpForm />
        </div>
      </div>
  );
}