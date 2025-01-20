using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using static LightTracking.Models.Sms;

namespace LightTracking.DTO
{
    public class Sms
    {
        public int Id { get; set; }

        public int UserId { get; set; }

        public String SmsText { get; set; }

        public String ContacName { get; set; }

        public String PhoneNumber { get; set; }

        public DateTime Date { get; set; }
        
        public TypeSms SmsType { get; set; }
    }
}