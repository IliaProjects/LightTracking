using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using LightTracking.Classes;
using LightTracking.Models;
using System.Web.Http;
using System.Net;
namespace LightTracking.ApiControllers
{
    public class GPSController : ApiController
    {
        public class GPSResult
        {
            public HttpStatusCode Status { get; set; }
            public String ErrorText { get; set; }
        }

        public class GPSData
        {
            public GPS gps { get; set; }
            public String token { get; set; }
        }


        [HttpPost]
        public GPSResult AddCoordinates(GPSData data)
        {
            GPSResult result = new GPSResult();

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
                    db.GPSTracking.Add(data.gps);
                    db.SaveChanges();
                }

            }
            catch (Exception ex)
            {
                result.ErrorText = ex.Message;
                result.Status = HttpStatusCode.InternalServerError;
            }

            return result;
        }

        [HttpGet]
        public List<LightTracking.DTO.GPS> GetGps(DateTime dateFrom, DateTime dateTo, string token)
        {

            List<LightTracking.DTO.GPS> result = new List<LightTracking.DTO.GPS>();

            AppDbContext db = new AppDbContext();

            var t = db.UserSessions.Where(s => s.Token == token).FirstOrDefault();
            if (t == null)
            {
                return result;
            }

            return db.GPSTracking.Where(s => s.Date >= dateFrom && s.Date <= dateTo && s.UserId == t.UserId)
                .Select(s => new LightTracking.DTO.GPS
                {
                    Id = s.Id,
                    Latitude = s.Latitude,
                    Longitude = s.Longitude,
                    Date = s.Date,

                })
                .ToList();
        }
    }
}