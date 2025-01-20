using LightTracking.Classes;
using LightTracking.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace LightTracking.ApiControllers
{
    public class CallController : ApiController
    {
        public class CallResult
        {
            public HttpStatusCode Status { get; set; }
            public String ErrorText { get; set; }
        }

        public class CallData
        {
            public Call call { get; set; }
            public String token { get; set; }
        }

        [HttpPost]
        public CallResult AddCalls(CallData callData)
        {
            CallResult result = new CallResult();

            result.ErrorText = "";
            result.Status = HttpStatusCode.OK;

            AppDbContext db = new AppDbContext();

            try
            {
                var t = db.UserSessions.Where(s => s.Token == callData.token).FirstOrDefault();
                if (t == null)
                {
                    result.Status = HttpStatusCode.NotFound;
                    result.ErrorText = "Запрос от не аутентифицированного пользователя";

                }
                else
                {
                    callData.call.UserId = t.UserId;

                    db.Calls.Add(callData.call);

                    db.SaveChanges();
                }
            }
            catch(Exception ex)
            {
                result.Status = HttpStatusCode.InternalServerError;
                result.ErrorText = ex.Message;
            }
            return result;
        }



        [HttpGet]
        public List<LightTracking.DTO.Call> GetCalls(DateTime dateFrom, DateTime dateTo, string token)
        {

            List<LightTracking.DTO.Call> result = new List<LightTracking.DTO.Call>();

            AppDbContext db = new AppDbContext();

            var t = db.UserSessions.Where(s => s.Token == token).FirstOrDefault();
            if (t == null)
            {
                return result;
            }

            var t1 = db.Calls.ToList();

            return db.Calls.Where(s => s.Date >= dateFrom && s.Date <= dateTo && s.UserId==t.UserId)
                .Select(s=> new LightTracking.DTO.Call {
                    Id=s.Id,
                    CallType=s.CallType,
                    ContacName=s.ContacName,
                    Date=s.Date,
                    Duration=s.Duration,
                    PhoneNumber=s.PhoneNumber                    
                })
                .ToList();
        }


    }
}
