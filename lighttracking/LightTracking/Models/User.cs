using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace LightTracking.Models
{
    public class User
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public String Name { get; set; }

        [Required]
        public String Login { get; set; }

        [Required]
        public String Password { get; set; }

        public virtual List<UserSession> UserSessions { get; set; }
        public virtual List<Call> Calls { get; set; }

    }
}