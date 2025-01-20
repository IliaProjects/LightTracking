using LightTracking.Classes;
using LightTracking.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace LightTracking.Controllers
{
    public class SmsController : Controller
    {
        public enum SmsFilter
        {
            All, Incomming, Outgoing
        }

        // GET: Sms
        public ActionResult Index()
        {
            return View();
        }

        [HttpPost]
        public ActionResult ListSms(DateTime dateFrom, DateTime dateTo, SmsFilter typeSms)
        {
            List<Sms> model = new List<Sms>();

            AppDbContext db = new AppDbContext();
            ViewBag.ShowTypeCall = false;

            switch (typeSms)
            {
                case SmsFilter.All:
                    ViewBag.ShowTypeCall = true;
                    model = db.SmsMessages.Where(s => s.Date >= dateFrom && s.Date <= dateTo).OrderBy(s => s.Date).ToList();
                    break;

                case SmsFilter.Incomming:

                    model = db.SmsMessages.Where(s => s.Date >= dateFrom && s.Date <= dateTo && s.SmsType == Sms.TypeSms.Incommig).OrderBy(s => s.Date).ToList();
                    break;

                case SmsFilter.Outgoing:

                    model = db.SmsMessages.Where(s => s.Date >= dateFrom && s.Date <= dateTo && s.SmsType== Sms.TypeSms.Outgouing).OrderBy(s => s.Date).ToList();
                    break;

                

                default:
                    break;
            }



            return PartialView(model);
        }
    }
}