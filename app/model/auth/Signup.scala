package model.auth

import play.api.data.Form
import play.api.mvc.Call

import form.auth.SignupFormData

case class ViewValueAuthSignup(
  form:    Form[SignupFormData],
  postUrl: Call
)
