using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace LightTracking.Models
{
    public class UserSession
    {
        [Key]
        public int Id { get; set; }

        public DateTime LastRequest { get; set; }

        public int UserId { get; set; }
            
        public String Token { get; set; }

        public virtual User User { get; set; }
    }
}