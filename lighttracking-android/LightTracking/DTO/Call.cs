using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using static LightTracking.Models.Call;

namespace LightTracking.DTO
{
    public class Call
    {
        public int Id { get; set; }

        public int UserId { get; set; }

        public String ContacName { get; set; }
        
        public String PhoneNumber { get; set; }
        
        public int Duration { get; set; }
        
        public DateTime Date { get; set; }
        
        public TypeCall CallType { get; set; }
    }
}