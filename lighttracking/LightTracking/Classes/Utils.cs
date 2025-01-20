using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace LightTracking.Classes
{
    public class Utils
    {
        public static bool IsUserAutenticated()
        {
            if (HttpContext.Current.Request.Cookies["my_token"] == null)
                return false;

            var token = HttpContext.Current.Request.Cookies["my_token"].Value;

            AppDbContext db = new AppDbContext();

            var row = db.UserSessions.Where(s => s.Token == token).FirstOrDefault();


            if (row == null)
                return false;
            else
            {
                if (HttpContext.Current.Session["UserInfo"] == null)
                {
                    HttpContext.Current.Session["UserInfo"] = db.Users.Find(row.UserId);
                }
                return true;
            }
        }
    }
}