// The following is a small hack to properly support hyperlinks as labels of accordion items
// where with accordion Bootstrap 5's accordion component is meant.
//
// Bootstrap's accordion component is very limited: It is not expected for the
// so-called “accordion button” which triggers the collapse/inflation to contain something else
// that can trigger an event like a hyperlink.
// More concretely, if the hyperlink (for example) is clicked, two events will fire:
// The one of the hyperlink and the one of the accordion component.
// In a better world, the component would be finer-grained only firing the collapse/inflation
// when the background or the arrow is clicked but not when the text/hyperlink is.
// 
// It is impossible or very inconvenient to somehow overwrite Bootstrap's event listeners after
// the fact. That's why this hack simply overwrites the visuals: It overrides the inflation
// animation if the hyperlink is clicked.
{
	const accordionAnchors = document.querySelectorAll('.accordion-button > a');
	
	for (const accordionAnchor of accordionAnchors) {
		accordionAnchor.addEventListener('click', (_event) => {
			const accordionButton = accordionAnchor.parentNode;
			accordionButton.classList.add('collapsed');
			const accordionHeader = accordionButton.parentNode;
			const accordionItem = accordionHeader.parentNode;
			const accordionCollapse = accordionItem.querySelector('.accordion-collapse');
			accordionCollapse.style.display = 'none';
		});
	}
}