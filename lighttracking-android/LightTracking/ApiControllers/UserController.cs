using LightTracking.Classes;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using static LightTracking.Controllers.UserController;

namespace LightTracking.ApiControllers
{
    public class UserController : ApiController
    {
        public class LoginResult
        {
            public String Token { get; set; }
            public HttpStatusCode Status { get; set; }
            public String Error { get; set; }
        }
        
        [HttpPost]
        public LoginResult  Login(LoginData data)
        {
            LoginResult result = new LoginResult();

            AppDbContext _db = new AppDbContext();

            var user = _db.Users.Where(s => s.Login == data.Login && s.Password == data.Password).FirstOrDefault();

            if (user == null)
            {
                result.Token = "";
                result.Status = HttpStatusCode.NotFound;
                result.Error = "Имя пользователя или пароль не верный";
                return result;
            }
            else
            {


                String t = Guid.NewGuid().ToString();

                _db.UserSessions.Add(new Models.UserSession
                {
                    Token = t,
                    LastRequest = DateTime.Now,
                    UserId = user.Id
                });

                _db.SaveChanges();

                result.Token = t;
                result.Status = HttpStatusCode.OK;
                result.Error = "";

                return result;
            }

            
        }

        [HttpPost]
        public HttpStatusCode Logout(LogoutData data)
        {
            AppDbContext _db = new AppDbContext();

            var userSession = _db.UserSessions.Where(s => s.Token == data.Token).FirstOrDefault();

            if (userSession != null)
            {
                _db.UserSessions.Remove(userSession);

                _db.SaveChanges();

                return HttpStatusCode.OK;
            }
            else
                return HttpStatusCode.NotFound;

        }
    }
}
