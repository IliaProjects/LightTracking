using LightTracking.Classes;
using LightTracking.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.Http;

namespace LightTracking.ApiControllers
{
    public class SmsController : ApiController
    {

        public class SmsResult
        {
            public HttpStatusCode Status { get; set; }
            public String ErrorText { get; set; }
        }

        public class SmsData
        {
            public Sms sms{ get; set; }
            public String token { get; set; }
        }


        [HttpPost]
        public SmsResult AddSms(SmsData data)
        {
            SmsResult result = new SmsResult();

            result.ErrorText = "";
            result.Status = HttpStatusCode.OK;

            AppDbContext db = new AppDbContext();

            try
            {
                var t = db.UserSessions.Where(s => s.Token == data.token).FirstOrDefault();
                if (t == null)
                {
                    result.Status = HttpStatusCode.NotFound;
                    result.ErrorText = "Запрос от не аутентифицированного пользователя";
                }
                else
                {
                    data.sms.UserId = t.UserId;
                    db.SmsMessages.Add(data.sms);
                    db.SaveChanges();
                }

            }
            catch(Exception ex)
            {
                result.ErrorText = ex.Message;
                result.Status = HttpStatusCode.InternalServerError;
            }

            return result;
        }

        [HttpGet]
        public List<LightTracking.DTO.Sms> GetSms(DateTime dateFrom, DateTime dateTo, string token)
        {

            List<LightTracking.DTO.Sms> result = new List<LightTracking.DTO.Sms>();

            AppDbContext db = new AppDbContext();

            var t = db.UserSessions.Where(s => s.Token == token).FirstOrDefault();
            if (t == null)
            {
                return result;
            }

            return db.SmsMessages.Where(s => s.Date >= dateFrom && s.Date <= dateTo && s.UserId == t.UserId)
                .Select(s => new LightTracking.DTO.Sms
                {
                    Id = s.Id,
                    SmsType=s.SmsType,
                    ContacName = s.ContacName,
                    Date = s.Date,
                    SmsText=s.SmsText,
                    PhoneNumber = s.PhoneNumber
                })
                .ToList();
        }
    }
}