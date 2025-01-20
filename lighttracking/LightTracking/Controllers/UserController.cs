using LightTracking.Classes;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.Mvc;

namespace LightTracking.Controllers
{
    public class UserController : Controller
    {
        private AppDbContext _db = null;

        public class LogoutData
        {
            public String Token{ get; set; }
        }

        public class LoginData
        {
            public String Login { get; set; }
            public String Password { get; set; }
        }

        public UserController()
        {
            _db = new AppDbContext();
        }

        [HttpGet]
        public ActionResult Login()
        {
            return View();
        }

        [HttpPost]
        public ActionResult Login(LoginData data)
        {
            var user = _db.Users.Where(s=> s.Login==data.Login && s.Password==data.Password).FirstOrDefault();

            if (user == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.NotFound);
            }
            else
            {
                Session["UserInfo"] = user;

                AppDbContext db = new AppDbContext();

                String t = Guid.NewGuid().ToString();

                db.UserSessions.Add(new Models.UserSession {
                    Token = t,
                    LastRequest=DateTime.Now,
                    UserId=user.Id
                });

                db.SaveChanges();

                HttpCookie token = new HttpCookie("my_token", t);
                token.Expires = DateTime.Now.AddDays(14);
                Response.SetCookie(token);

                return Redirect("~/");
            }
        }

        [HttpGet]
        public ActionResult Logout()
        {
            String token = Request.Cookies["my_token"].Value;

            var userSession = _db.UserSessions.Where(s => s.Token == token).FirstOrDefault();

            if (userSession != null)
            {
                _db.UserSessions.Remove(userSession);

                _db.SaveChanges();

                return Redirect("~/user/login");
            }

            return new HttpStatusCodeResult(HttpStatusCode.NotFound);

        }


    }
}