using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using static LightTracking.Models.GPS;
namespace LightTracking.DTO
{
    public class GPS
    {
        public int Id { get; set; }

        public int UserId { get; set; }

        public DateTime Date { get; set; }

        public String Latitude { get; set; }

        public String Longitude { get; set; }
    }
}