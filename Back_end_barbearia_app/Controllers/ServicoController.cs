using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Back_end_barbearia_app.Models;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Back_end_barbearia_app.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class ServicoController : ControllerBase
    {
        private readonly BarberTechContext _context;

        public ServicoController(BarberTechContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<Servico>>> GetAll() =>
            await _context.Servicos.ToListAsync();

        [HttpGet("{id}")]
        public async Task<ActionResult<Servico>> GetById(int id)
        {
            var entity = await _context.Servicos.FindAsync(id);
            return entity == null ? NotFound() : entity;
        }

        [HttpPost]
        public async Task<ActionResult<Servico>> Create(Servico s)
        {
            _context.Servicos.Add(s);
            await _context.SaveChangesAsync();
            return CreatedAtAction(nameof(GetById), new { id = s.Id }, s);
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> Update(int id, Servico s)
        {
            if (id != s.Id) return BadRequest();
            _context.Entry(s).State = EntityState.Modified;
            await _context.SaveChangesAsync();
            return NoContent();
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var entity = await _context.Servicos.FindAsync(id);
            if (entity == null) return NotFound();
            _context.Servicos.Remove(entity);
            await _context.SaveChangesAsync();
            return NoContent();
        }
    }
}
