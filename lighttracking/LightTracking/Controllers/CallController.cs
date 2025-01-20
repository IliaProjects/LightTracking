using LightTracking.Classes;
using LightTracking.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using static LightTracking.Models.Call;

namespace LightTracking.Controllers
{
    public class CallController : Controller
    {
        public enum CallFilter
        {
             All, Incomming, Outgoing, Missed
        }

        // GET: Call
        public ActionResult Index()
        {
            return View();
        }

        [HttpPost]
        public ActionResult ListCalls(DateTime dateFrom, DateTime dateTo, CallFilter typeCall)
        {
            List<Call> model = new List<Call>();

            AppDbContext db = new AppDbContext();
            ViewBag.ShowTypeCall = false;

            switch (typeCall)
            {
                case CallFilter.All:
                    ViewBag.ShowTypeCall = true;
                    model = db.Calls.Where(s => s.Date >= dateFrom && s.Date <= dateTo).OrderBy(s=>s.Date).ToList();
                    break;

                case CallFilter.Incomming:

                    model = db.Calls.Where(s => s.Date >= dateFrom && s.Date <= dateTo && s.CallType==TypeCall.Incommig).OrderBy(s => s.Date).ToList();
                    break;

                case CallFilter.Outgoing:

                    model = db.Calls.Where(s => s.Date >= dateFrom && s.Date <= dateTo && s.CallType == TypeCall.Outgouing).OrderBy(s => s.Date).ToList();
                    break;

                case CallFilter.Missed:

                    model = db.Calls.Where(s => s.Date >= dateFrom && s.Date <= dateTo && s.CallType == TypeCall.Missed).OrderBy(s => s.Date).ToList();
                    break;

                default:
                    break;
            }

            

            return PartialView(model);
        }
    }
}