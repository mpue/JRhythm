varying vec3 lightDir,normal;
uniform sampler2D tex;
varying vec2 vTexCoord;
 
const float blurSize = 0.5/512.0; 

void main()
{
	vec3 ct,cf;
	vec4 texel;
	float intensity,at,af;
	intensity = max(dot(lightDir,normalize(normal)),0.0);

	cf = intensity * (gl_FrontMaterial.diffuse).rgb + gl_FrontMaterial.ambient.rgb;
	af = gl_FrontMaterial.diffuse.a;
	texel = texture2D(tex,gl_TexCoord[0].st);
	
	
 
   // blur in y (vertical)
   // take nine samples, with the distance blurSize between them
   
   texel += texture2D(tex, vec2(vTexCoord.x - 4.0*blurSize, vTexCoord.y)) * 0.05;
   texel += texture2D(tex, vec2(vTexCoord.x - 3.0*blurSize, vTexCoord.y)) * 0.09;
   texel += texture2D(tex, vec2(vTexCoord.x - 2.0*blurSize, vTexCoord.y)) * 0.12;
   texel += texture2D(tex, vec2(vTexCoord.x - blurSize, vTexCoord.y)) * 0.15;
   texel += texture2D(tex, vec2(vTexCoord.x, vTexCoord.y)) * 0.16;
   texel += texture2D(tex, vec2(vTexCoord.x + blurSize, vTexCoord.y)) * 0.15;
   texel += texture2D(tex, vec2(vTexCoord.x + 2.0*blurSize, vTexCoord.y)) * 0.12;
   texel += texture2D(tex, vec2(vTexCoord.x + 3.0*blurSize, vTexCoord.y)) * 0.09;
   texel += texture2D(tex, vec2(vTexCoord.x + 4.0*blurSize, vTexCoord.y)) * 0.05;

        
   texel += texture2D(tex, vec2(vTexCoord.x, vTexCoord.y - 4.0*blurSize)) * 0.05;
   texel += texture2D(tex, vec2(vTexCoord.x, vTexCoord.y - 3.0*blurSize)) * 0.09;
   texel += texture2D(tex, vec2(vTexCoord.x, vTexCoord.y - 2.0*blurSize)) * 0.12;
   texel += texture2D(tex, vec2(vTexCoord.x, vTexCoord.y - blurSize)) * 0.15;
   texel += texture2D(tex, vec2(vTexCoord.x, vTexCoord.y)) * 0.16;
   texel += texture2D(tex, vec2(vTexCoord.x, vTexCoord.y + blurSize)) * 0.15;
   texel += texture2D(tex, vec2(vTexCoord.x, vTexCoord.y + 2.0*blurSize)) * 0.12;
   texel += texture2D(tex, vec2(vTexCoord.x, vTexCoord.y + 3.0*blurSize)) * 0.09;
   texel += texture2D(tex, vec2(vTexCoord.x, vTexCoord.y + 4.0*blurSize)) * 0.05;

   texel *= 0.5;

	ct = texel.rgb * .4;
	at = texel.a;
	gl_FragColor = vec4(ct * cf, at * af);

}