package model.auth

import play.api.data.Form
import play.api.mvc.Call

import form.auth.LoginFormData

case class ViewValueAuthLogin(
  form:    Form[LoginFormData],
  postUrl: Call
)
