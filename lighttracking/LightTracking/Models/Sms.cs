using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace LightTracking.Models
{
    public class Sms
    {
        public enum TypeSms
        {
            Incommig, Outgouing
        }


        [Key]
        public int Id { get; set; }

        [Required]
        public int UserId { get; set; }

        [Required]
        public String SmsText { get; set; }

        public String ContacName { get; set; }

        [Required]
        public String PhoneNumber{ get; set; }

        [Required]
        public DateTime Date{ get; set; }

        [Required]
        public TypeSms SmsType { get; set; }

        public virtual User User { get; set; }

    }
}