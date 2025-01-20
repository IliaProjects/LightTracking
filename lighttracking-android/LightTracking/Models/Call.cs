using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace LightTracking.Models
{
    public class Call
    {
        public enum TypeCall
        {
            Incommig, Outgouing, Missed
        }


        [Key]
        public int Id { get; set; }
        
        public int UserId { get; set; }

        public String ContacName { get; set; }

        [Required]
        public String PhoneNumber{ get; set; }

        [Required]
        public int Duration{ get; set; }

        [Required]
        public DateTime Date{ get; set; }

        [Required]
        public TypeCall CallType { get; set; }

        public virtual User User { get; set; }

    }
}