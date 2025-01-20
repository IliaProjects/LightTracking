using LightTracking.Classes;
using LightTracking.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace LightTracking.Controllers
{
    public class GpsController : Controller
    {
        // GET: Gps
        public ActionResult Index()
        {
            return View();
        }

        [HttpPost]
        public ActionResult ListGpsCoordinates(DateTime dateFrom, DateTime dateTo)
        {
            AppDbContext db = new AppDbContext();

            var result = db.GPSTracking.Where(s=>s.Date>=dateFrom && s.Date<=dateTo)
                .Select(s => new
                {
                    Date = s.Date,
                    Latitude = s.Latitude,
                    Longitude = s.Longitude
                })
                .ToList();

            return Json(result);
        }

    }
}