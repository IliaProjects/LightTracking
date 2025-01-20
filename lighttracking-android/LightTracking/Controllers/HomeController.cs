using LightTracking.Classes;
using LightTracking.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace LightTracking.Controllers
{
    public class HomeController : Controller
    {
        
        public ActionResult Index()
        {
            if (!Utils.IsUserAutenticated())
                return Redirect("~/user/login");

            ViewBag.UserName = ((User)Session["UserInfo"]).Name;

            return View();
        }
    }
}