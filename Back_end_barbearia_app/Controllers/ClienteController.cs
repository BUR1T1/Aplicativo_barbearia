using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Back_end_barbearia_app.Models;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Back_end_barbearia_app.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class ClienteController : ControllerBase
    {
        private readonly BarberTechContext _context;

        public ClienteController(BarberTechContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<Cliente>>> GetAll() =>
            await _context.Clientes.ToListAsync();

        [HttpGet("{id}")]
        public async Task<ActionResult<Cliente>> GetById(int id)
        {
            var entity = await _context.Clientes.FindAsync(id);
            return entity == null ? NotFound() : entity;
        }

        [HttpPost]
        public async Task<ActionResult<Cliente>> Create(Cliente c)
        {
            _context.Clientes.Add(c);
            await _context.SaveChangesAsync();
            return CreatedAtAction(nameof(GetById), new { id = c.Id }, c);
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> Update(int id, Cliente c)
        {
            if (id != c.Id) return BadRequest();
            _context.Entry(c).State = EntityState.Modified;
            await _context.SaveChangesAsync();
            return NoContent();
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var entity = await _context.Clientes.FindAsync(id);
            if (entity == null) return NotFound();
            _context.Clientes.Remove(entity);
            await _context.SaveChangesAsync();
            return NoContent();
        }
    }
}
